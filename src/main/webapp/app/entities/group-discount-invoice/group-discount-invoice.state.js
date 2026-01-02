(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('group-discount-invoice', {
                parent: 'administration',
                url: '/group-discount-invoice',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.groupDiscountInvoice.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/group-discount-invoice/group-discount-invoices.html',
                        controller: 'GroupDiscountInvoiceController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('groupDiscountInvoice');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('group-discount-invoice.set-payment-date', {
                parent: 'group-discount-invoice',
                url: '/{id}/set-payment-date',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/group-discount-invoice/group-discount-invoice-set-payment-date-dialog.html',
                        controller: 'GroupDiscountInvoiceSetPaymentDateDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    paymentDate: new Date(),
                                    id: $stateParams.id
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('group-discount-invoice', null, {reload: 'group-discount-invoice'});
                    }, function () {
                        $state.go('group-discount-invoice');
                    });
                }]
            })
            .state('group-discount-invoice.storno', {
                parent: 'group-discount-invoice',
                url: '/{id}/storno',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/group-discount-invoice/group-discount-invoice-storno-dialog.html',
                        controller: 'GroupDiscountInvoiceStornoController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            entity: ['GroupDiscountInvoice', function(GroupDiscountInvoice) {
                                return GroupDiscountInvoice.get({id : $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('group-discount-invoice', null, { reload: 'group-discount-invoice' });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }

})();
