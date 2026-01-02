(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('credit-card-report', {
                parent: 'maintenance',
                url: '/credit-card-report',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.creditCardReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/credit-card-report/credit-card-reports.html',
                        controller: 'CreditCardReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: function () {
                        return {
                            transactionId: null,
                            programNumber: null,
                            fromDate: null,
                            toDate: null
                        };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('creditCardReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
