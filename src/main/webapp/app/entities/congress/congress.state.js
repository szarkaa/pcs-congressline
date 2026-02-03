(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('congress', {
            parent: 'maintenance',
            url: '/congress',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'pcsApp.congress.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/congress/congresses.html',
                    controller: 'CongressController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('congress');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('congress.new', {
            parent: 'congress',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/congress/congress-dialog.html',
                    controller: 'CongressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                meetingCode: null,
                                name: null,
                                startDate: null,
                                endDate: null,
                                contactPerson: null,
                                contactEmail: null,
                                website: null,
                                programNumber: null,
                                defaultCountryId: null,
                                additionalBillingTextHu: null,
                                additionalBillingTextEn: null,
                                migratedFromCongressCode: null,
                                archive: false,
                                currencies: [],
                                onlineRegCurrencies: [],
                                bankAccounts: []
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('congress', null, { reload: 'congress' });
                }, function() {
                    $state.go('congress');
                });
            }]
        })
        .state('congress.edit', {
            parent: 'congress',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/congress/congress-dialog.html',
                    controller: 'CongressDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Congress', function(Congress) {
                            return Congress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('congress', null, { reload: 'congress' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('congress.online-reg-config', {
            parent: 'congress',
            url: '/{id}/online-reg-config',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/congress/online-reg-config-dialog.html',
                    controller: 'OnlineRegConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('paymentSupplier');
                            $translatePartialLoader.addPart('onlineRegConfig');
                            return $translate.refresh();
                        }],
                        congress: ['Congress', function(Congress) {
                            return Congress.get({id : $stateParams.id}).$promise;
                        }],
                        entity: ['Congress', function(Congress) {
                            return Congress.getOnlineRegConfig({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('congress', null, { reload: 'congress' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('congress.delete', {
            parent: 'congress',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/congress/congress-delete-dialog.html',
                    controller: 'CongressDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Congress', function(Congress) {
                            return Congress.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('congress', null, { reload: 'congress' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
