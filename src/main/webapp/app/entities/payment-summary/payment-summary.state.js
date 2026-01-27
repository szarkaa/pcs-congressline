(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('payment-summary', {
                parent: 'report',
                url: '/payment-summary',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.paymentSummary.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/payment-summary/payment-summarys.html',
                        controller: 'PaymentSummaryController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('paymentSummary');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
