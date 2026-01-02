(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('maintenance', {
            abstract: true,
            url: '/maintenance',
            parent: 'app'
        });
    }
})();
