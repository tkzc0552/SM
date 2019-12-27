const utils = require('./utils');
const config = require('../config');
const isProduction = process.env.NODE_ENV === 'production';
const isIntegration = process.env.NODE_ENV === 'integration';

module.exports = {
    loaders: utils.cssLoaders({
        sourceMap: isProduction
            ? config.build.productionSourceMap
            : (isIntegration ? config.test.productionSourceMap : config.dev.cssSourceMap),
        extract: isProduction
    }),
    postcss: [
        require('autoprefixer')({
            browsers: ['last 2 versions']
        })
    ]
};
