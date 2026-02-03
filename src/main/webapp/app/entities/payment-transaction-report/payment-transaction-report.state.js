(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('payment-transaction-report', {
                parent: 'maintenance',
                url: '/payment-transaction-report',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'pcsApp.paymentTransactionReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/payment-transaction-report/payment-transaction-reports.html',
                        controller: 'PaymentTransactionReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: function () {
                        return {
                            transactionId: null,
                            orderNumber: null,
                            fromDate: null,
                            toDate: null
                        };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('paymentTransactionReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('payment-transaction-report.refund', {
                parent: 'payment-transaction-report',
                url: '/{id}/refund',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/payment-transaction-report/payment-transaction-report-refund-dialog.html',
                        controller: 'PaymentTransactionRefundDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['PaymentTransactionReport', function(PaymentTransactionReport) {
                                return PaymentTransactionReport.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('payment-transaction-report');
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('payment-transaction-report.show-payment-refund-transaction', {
                parent: 'payment-transaction-report',
                url: '/{id}/show-payment-refund-transaction',
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/payment-transaction-report/show-payment-refund-transaction-dialog.html',
                        controller: 'ShowPaymentRefundTransactionDialogController',
                        controllerAs: 'vm',
                        size: 'lg',
                        resolve: {
                            refunds: ['PaymentTransactionReport', function (PaymentTransactionReport) {
                                return PaymentTransactionReport.queryPaymentRefundTransactionsByTrxId({id: $stateParams.id});
                            }]
                        }
                    }).result.then(function() {
                        $state.go('payment-transaction-report');
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }


})();
