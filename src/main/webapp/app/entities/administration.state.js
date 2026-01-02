(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('administration', {
            abstract: true,
            url: '/administration',
            parent: 'app'
        });
    }
})();
