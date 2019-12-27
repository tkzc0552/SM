package com.zhm.essential.id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.jdbc.AbstractReturningWork;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 扩展hibernate生成id的生成器
 *
 * @author geewit
 * @since 2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class CalendarFormatTableGenerator extends TableGenerator {
    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
            CoreMessageLogger.class,
            CalendarFormatTableGenerator.class.getName()
    );

    public static final String UPDATE_DATE_PARAM = "update_date";

    public static final String NUMBER_FORMAT_PARAM = "format";

    public static final String PREFIX_PARAM = "prefix";


    private String refreshQuery;

    private String formatValue;

    private String prefixValue;

    private String updateDateColumnName;


    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(new LongType(), params, serviceRegistry);
        this.formatValue = params.getProperty(NUMBER_FORMAT_PARAM);
        this.prefixValue = params.getProperty(PREFIX_PARAM);
        this.updateDateColumnName = params.getProperty(UPDATE_DATE_PARAM);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        LOG.log(Logger.Level.DEBUG, "segmentValue = " + segmentValue);
        Serializable generated;
        final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
                .getService(JdbcServices.class)
                .getSqlStatementLogger();
        final SessionEventListenerManager statsCollector = session.getEventListenerManager();
        final Calendar now = Calendar.getInstance(Locale.CHINA);
        final java.sql.Date nowDate = new java.sql.Date(now.getTimeInMillis());
        generated = optimizer.generate(
                new AccessCallback() {
                    @Override
                    public IntegralDataTypeHolder getNextValue() {
                        return session.getTransactionCoordinator().createIsolationDelegate().delegateWork(
                                new AbstractReturningWork<IntegralDataTypeHolder>() {
                                    @Override
                                    public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
                                        final IntegralDataTypeHolder value = makeValue();
                                        int rows;

                                        do {
                                            try (PreparedStatement selectPS = prepareStatement(connection, selectQuery, statementLogger, statsCollector)) {
                                                selectPS.setString(1, segmentValue);
                                                final ResultSet selectRS = executeQuery(selectPS, statsCollector);
                                                boolean needInitialize = false;
                                                boolean needRefresh = false;
                                                if (!selectRS.next()) {
                                                    needInitialize = true;
                                                } else if (selectRS.wasNull()) {
                                                    needInitialize = true;
                                                }
                                                if (needInitialize) {
                                                    value.initialize(initialValue);
                                                    try (PreparedStatement insertPS = prepareStatement(connection, insertQuery, statementLogger, statsCollector)) {
                                                        insertPS.setString(1, segmentValue);
                                                        value.bind(insertPS, 2);
                                                        insertPS.setDate(3, nowDate);
                                                        executeUpdate(insertPS, statsCollector);
                                                    }
                                                } else {
                                                    Date updateDate = selectRS.getDate(2);
                                                    if (updateDate == null) {
                                                        value.initialize(initialValue);
                                                        needRefresh = true;
                                                    } else if (!DateUtils.isSameDay(DateUtils.toCalendar(updateDate), now)) {
                                                        value.initialize(initialValue);
                                                        needRefresh = true;
                                                    } else {
                                                        value.initialize(selectRS.getLong(1));
                                                    }
                                                }
                                                selectRS.close();
                                                if(needRefresh) {
                                                    try (PreparedStatement refreshPS = prepareStatement(connection, refreshQuery, statementLogger, statsCollector)) {
                                                        final IntegralDataTypeHolder updateValue = value.copy();
                                                        if (optimizer.applyIncrementSizeToSourceValues()) {
                                                            updateValue.add(incrementSize);
                                                        } else {
                                                            updateValue.increment();
                                                        }
                                                        updateValue.bind(refreshPS, 1);
                                                        refreshPS.setDate(2, nowDate);
                                                        refreshPS.setString(3, segmentValue);
                                                        rows = executeUpdate(refreshPS, statsCollector);
                                                    } catch (SQLException e) {
                                                        LOG.unableToUpdateQueryHiValue(renderedTableName, e);
                                                        throw e;
                                                    }
                                                } else {
                                                    try (PreparedStatement updatePS = prepareStatement(connection, updateQuery, statementLogger, statsCollector)) {
                                                        final IntegralDataTypeHolder updateValue = value.copy();
                                                        if (optimizer.applyIncrementSizeToSourceValues()) {
                                                            updateValue.add(incrementSize);
                                                        } else {
                                                            updateValue.increment();
                                                        }
                                                        updateValue.bind(updatePS, 1);
                                                        updatePS.setDate(2, nowDate);
                                                        value.bind(updatePS, 3);
                                                        updatePS.setString(4, segmentValue);
                                                        rows = executeUpdate(updatePS, statsCollector);
                                                    } catch (SQLException e) {
                                                        LOG.unableToUpdateQueryHiValue(renderedTableName, e);
                                                        throw e;
                                                    }
                                                }
                                            } catch (SQLException e) {
                                                LOG.unableToReadOrInitHiValue(e);
                                                throw e;
                                            }
                                        } while (rows == 0);

                                        accessCount++;

                                        return value;
                                    }
                                },
                                true
                        );
                    }

                    @Override
                    public String getTenantIdentifier() {
                        return session.getTenantIdentifier();
                    }
                }
        );
        if (generated instanceof Number) {
            StringBuilder builder;
            if(StringUtils.isNotBlank(prefixValue)) {
                builder = new StringBuilder(prefixValue);
            } else {
                builder = new StringBuilder();
            }
            if (formatValue == null) {
                builder.append(generated);
            } else {
                builder.append(String.format("%2$tY%2$tm%2$td" + formatValue, generated, now));
            }
            return builder.toString();
        }
        return generated;
    }

    @Override
    public void registerExportables(Database database) {
        super.registerExportables(database);
        this.refreshQuery = buildRefreshQuery();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String buildSelectQuery(Dialect dialect) {
        final String alias = "tbl";
        final String query = "select " + StringHelper.qualify(alias, valueColumnName) + ", " + StringHelper.qualify(alias, this.updateDateColumnName) +
                " from " + renderedTableName + ' ' + alias +
                " where " + StringHelper.qualify(alias, segmentColumnName) + "=?";
        final LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        lockOptions.setAliasSpecificLockMode(alias, LockMode.PESSIMISTIC_WRITE);
        final Map<String, String[]> updateTargetColumnsMap = Collections.singletonMap(alias, new String[]{valueColumnName});
        String sql = dialect.applyLocksToSql(query, lockOptions, updateTargetColumnsMap);
        LOG.debug("selectQuery = " + sql);
        return sql;
    }

    @Override
    protected String buildUpdateQuery() {
        return "update " + renderedTableName +
                " set " + valueColumnName + "=?, " + this.updateDateColumnName + "=?" +
                " where " + valueColumnName + "=? and " + segmentColumnName + "=?";
    }

    private String buildRefreshQuery() {
        return "update " + renderedTableName +
                " set " + valueColumnName + "=?, " + this.updateDateColumnName + "=?" +
                " where " + segmentColumnName + "=?";
    }

    @Override
    protected String buildInsertQuery() {
        return "insert into " + renderedTableName + " (" + segmentColumnName + ", " + valueColumnName + ", " + this.updateDateColumnName + ") " + " values (?, ?, ?)";
    }

    private PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            SqlStatementLogger statementLogger,
            SessionEventListenerManager statsCollector) throws SQLException {
        statementLogger.logStatement(sql, FormatStyle.BASIC.getFormatter());
        try {
            statsCollector.jdbcPrepareStatementStart();
            return connection.prepareStatement(sql);
        } finally {
            statsCollector.jdbcPrepareStatementEnd();
        }
    }

    private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            return ps.executeUpdate();
        } finally {
            statsCollector.jdbcExecuteStatementEnd();
        }
    }

    private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
        try {
            statsCollector.jdbcExecuteStatementStart();
            return ps.executeQuery();
        } finally {
            statsCollector.jdbcExecuteStatementEnd();
        }
    }

    private IntegralDataTypeHolder makeValue() {
        return IdentifierGeneratorHelper.getIntegralDataTypeHolder(identifierType.getReturnedClass());
    }
}
