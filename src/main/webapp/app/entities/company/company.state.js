(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('company', {
                parent: 'maintenance',
                url: '/company',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.company.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/company/company-dialog.html',
                        controller: 'CompanyController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['Company', function (Company) {
                        return Company.get();
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('company');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }

})();
