(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('online-reg-discount-code', {
            parent: 'maintenance',
            url: '/congress/{congressId}/online-reg-discount-codes',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'pcsApp.onlineRegDiscountCode.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/online-reg-discount-code/online-reg-discount-codes.html',
                    controller: 'OnlineRegDiscountCodeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                congress: ['$stateParams', 'Congress', function ($stateParams, Congress) {
                    return Congress.get({id: $stateParams.congressId}).$promise;
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('onlineRegDiscountCode');
                    $translatePartialLoader.addPart('onlineDiscountCodeType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('online-reg-discount-code.new', {
            parent: 'online-reg-discount-code',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-discount-code/online-reg-discount-code-dialog.html',
                    controller: 'OnlineRegDiscountCodeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                code: null,
                                discountPercentage: null,
                                discountType: null,
                                congressId: $stateParams.congressId
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('online-reg-discount-code', null, { reload: 'online-reg-discount-code' });
                }, function() {
                    $state.go('online-reg-discount-code');
                });
            }]
        })
        .state('online-reg-discount-code.edit', {
            parent: 'online-reg-discount-code',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-discount-code/online-reg-discount-code-dialog.html',
                    controller: 'OnlineRegDiscountCodeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OnlineRegDiscountCode', function (OnlineRegDiscountCode) {
                            return OnlineRegDiscountCode.get({id: $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('online-reg-discount-code', null, { reload: 'online-reg-discount-code' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('online-reg-discount-code.delete', {
            parent: 'online-reg-discount-code',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-discount-code/online-reg-discount-code-delete-dialog.html',
                    controller: 'OnlineRegDiscountCodeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OnlineRegDiscountCode', function(OnlineRegDiscountCode) {
                            return OnlineRegDiscountCode.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('online-reg-discount-code', null, { reload: 'online-reg-discount-code' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
