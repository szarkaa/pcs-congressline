(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('report', {
            abstract: true,
            url: '/report',
            parent: 'app'
        });
    }
})();
