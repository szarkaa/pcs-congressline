(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('backend-online-reg', {
            parent: 'administration',
            url: '/backend-online-reg',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.backendOnlineReg.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/backend-online-reg/backend-online-regs.html',
                    controller: 'BackendOnlineRegController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                onlineRegFilter: function () {
                    return {
                        selectedOnlineRegIds: {}
                    }
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('onlineReg');
                    $translatePartialLoader.addPart('backendOnlineReg');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('backend-online-reg.accept', {
            parent: 'backend-online-reg',
            url: '/{id}/accept',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/backend-online-reg/backend-online-reg-dialog.html',
                    controller: 'BackendOnlineRegDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['BackendOnlineReg', '$stateParams', function(BackendOnlineReg, $stateParams) {
                    return BackendOnlineReg.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('backend-online-reg.accept-confirmation', {
            parent: 'backend-online-reg.accept',
            url: '/{id}/accept/confirmation',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'entity', function($stateParams, $state, $uibModal, entity) {
                $uibModal.open({
                    templateUrl: 'app/entities/backend-online-reg/backend-online-reg-confirmation-dialog.html',
                    controller: 'BackendOnlineRegConfirmationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return entity;
                        }
                    }
                }).result.then(function() {
                    $state.go('backend-online-reg', null, { reload: 'backend-online-reg' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('backend-online-reg.accept-confirmation-all', {
            parent: 'backend-online-reg',
            url: '/accept-all/confirmation',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'onlineRegFilter', function($stateParams, $state, $uibModal, onlineRegFilter) {
                $uibModal.open({
                    templateUrl: 'app/entities/backend-online-reg/backend-online-reg-confirmation-all-dialog.html',
                    controller: 'BackendOnlineRegConfirmationAllDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        onlineRegFilter: function() {
                            return onlineRegFilter;
                        }
                    }
                }).result.then(function() {
                    $state.go('backend-online-reg', null, { reload: 'backend-online-reg' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('backend-online-reg.delete-confirmation-all', {
            parent: 'backend-online-reg',
            url: '/delete-all/confirmation',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'onlineRegFilter', function($stateParams, $state, $uibModal, onlineRegFilter) {
                $uibModal.open({
                    templateUrl: 'app/entities/backend-online-reg/backend-online-reg-delete-all-dialog.html',
                    controller: 'BackendOnlineRegDeleteAllDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        onlineRegFilter: function() {
                            return onlineRegFilter;
                        }
                    }
                }).result.then(function() {
                    $state.go('backend-online-reg', null, { reload: 'backend-online-reg' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('backend-online-reg.delete', {
            parent: 'backend-online-reg',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/backend-online-reg/backend-online-reg-delete-dialog.html',
                    controller: 'BackendOnlineRegDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BackendOnlineReg', function(BackendOnlineReg) {
                            return BackendOnlineReg.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('backend-online-reg', null, { reload: 'backend-online-reg' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
