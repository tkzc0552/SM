const getters = {
    sidebar: state => state.app.sidebar,
    language: state => state.app.language,
    size: state => state.app.size,

    visitedViews: state => state.tags.visitedViews,
    cachedViews: state => state.tags.cachedViews,
};
export default getters
