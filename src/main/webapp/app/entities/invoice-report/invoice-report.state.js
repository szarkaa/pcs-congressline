(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('invoice-report', {
                parent: 'maintenance',
                url: '/invoice-report',
                data: {
                    authorities: ['ROLE_ACCOUNTANT','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.invoiceReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/invoice-report/invoice-reports.html',
                        controller: 'InvoiceReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: function () {
                        return {
                            programNumber: null,
                            invoiceNumber: null,
                            fromDate: null,
                            toDate: null,
                            filterProforma: true
                        };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('invoiceType');
                        $translatePartialLoader.addPart('invoiceReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('invoice-report.send-to-nav', {
                parent: 'invoice-report',
                url: '/{id}/send-to-nav',
                data: {
                    authorities: ['ROLE_ACCOUNTANT','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/invoice-report/invoice-report-send-to-nav-confirm-dialog.html',
                        controller: 'InvoiceReportSendToNavConfirmController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: function () {
                                return {
                                    id: $stateParams.id
                                };
                            }
                        }
                    }).result.then(function() {
                        $state.go('invoice-report');
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('invoice-report.show-nav-sending-status', {
                parent: 'invoice-report',
                url: '/{id}/show-nav-sending-status',
                data: {
                    authorities: ['ROLE_ACCOUNTANT','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/invoice-report/show-nav-sending-status-dialog.html',
                        controller: 'ShowNavSendingStatusController',
                        controllerAs: 'vm',
                        size: 'lg',
                        resolve: {
                            invoiceNavStatusArray: ['InvoiceReport', function (InvoiceReport) {
                                return InvoiceReport.queryInvoiceNavStatusById({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('invoice-report');
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }


})();
