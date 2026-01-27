(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('group-discount-payment', {
            parent: 'group-discount',
            url: '/group-discount-payment',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.groupDiscountPayment.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/group-discount-payment/group-discount-payments.html',
                    controller: 'GroupDiscountPaymentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('groupDiscountPayment');
                    $translatePartialLoader.addPart('chargeableItemType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('group-discount-payment-detail.edit', {
            parent: 'group-discount-payment-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-payment/group-discount-payment-dialog.html',
                    controller: 'GroupDiscountPaymentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupDiscountPayment', function(GroupDiscountPayment) {
                            return GroupDiscountPayment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-discount-payment.new', {
            parent: 'group-discount-payment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-payment/group-discount-payment-dialog.html',
                    controller: 'GroupDiscountPaymentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                paymentType: null,
                                dateOfPayment: new Date(),
                                amount: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('group-discount-payment', null, { reload: 'group-discount-payment' });
                }, function() {
                    $state.go('group-discount-payment');
                });
            }]
        })
        .state('group-discount-payment.edit', {
            parent: 'group-discount-payment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-payment/group-discount-payment-dialog.html',
                    controller: 'GroupDiscountPaymentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupDiscountPayment', function(GroupDiscountPayment) {
                            return GroupDiscountPayment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-discount-payment', null, { reload: 'group-discount-payment' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-discount-payment.delete', {
            parent: 'group-discount-payment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-discount-payment/group-discount-payment-delete-dialog.html',
                    controller: 'GroupDiscountPaymentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['GroupDiscountPayment', function(GroupDiscountPayment) {
                            return GroupDiscountPayment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-discount-payment', null, { reload: 'group-discount-payment' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
