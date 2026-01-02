(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('financial-report', {
                parent: 'report',
                url: '/financial-report',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.financialReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/financial-report/financial-reports.html',
                        controller: 'FinancialReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: ['CongressSelector', function (CongressSelector) {
                        return {
                            participantsToPay: false,
                            congressId: CongressSelector.getSelectedCongress().id
                        };
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('financialReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
