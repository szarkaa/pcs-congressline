(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('rate', {
            parent: 'maintenance',
            url: '/rate',
            data: {
                authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                pageTitle: 'pcsApp.rate.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/rate/rates.html',
                    controller: 'RateController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('rate');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('rate.new', {
            parent: 'rate',
            url: '/new',
            data: {
                authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rate/rate-dialog.html',
                    controller: 'RateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                rate: null,
                                valid: new Date(),
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('rate', null, { reload: 'rate' });
                }, function() {
                    $state.go('rate');
                });
            }]
        })
        .state('rate.edit', {
            parent: 'rate',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rate/rate-dialog.html',
                    controller: 'RateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Rate', function(Rate) {
                            return Rate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('rate', null, { reload: 'rate' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('rate.delete', {
            parent: 'rate',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/rate/rate-delete-dialog.html',
                    controller: 'RateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Rate', function(Rate) {
                            return Rate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('rate', null, { reload: 'rate' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
