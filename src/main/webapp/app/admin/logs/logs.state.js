(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('logs', {
            parent: 'system',
            url: '/logs',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'logs.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/admin/logs/logs.html',
                    controller: 'LogsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('logs');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
