(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('jhi-health', {
            parent: 'system',
            url: '/health',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'health.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/admin/health/health.html',
                    controller: 'JhiHealthCheckController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('health');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
