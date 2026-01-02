(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('group-discount', {
            abstract: true,
            url: '/group-discount',
            parent: 'app',
            resolve: {
                groupDiscountItemFilter: ['GroupDiscountItemFilter', function (GroupDiscountItemFilter) {
                    return GroupDiscountItemFilter.getGroupDiscountItemFilter();
                }]
            }
        });
    }
})();
