(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('vat-info', {
            parent: 'maintenance',
            url: '/vat-info',
            params: {
                congressSpecific: false
            },
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.vatInfo.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/vat-info/vat-infos.html',
                    controller: 'VatInfoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                congressSpecific: ['$stateParams', function ($stateParams) {
                    return $stateParams.congressSpecific || false;
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('vatInfo');
                    $translatePartialLoader.addPart('chargeableItemType');
                    $translatePartialLoader.addPart('vatRateType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('vat-info.new', {
            parent: 'vat-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'congressSpecific', 'CongressSelector', function($stateParams, $state, $uibModal, congressSpecific, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-dialog.html',
                    controller: 'VatInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                vat: null,
                                szj: null,
                                chargeableItemType: null,
                                vatRateType: null,
                                vatExceptionReason: null,
                                id: null,
                                congress: congressSpecific ? CongressSelector.getSelectedCongress() : null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('vat-info', null, { reload: 'vat-info' });
                }, function() {
                    $state.go('vat-info');
                });
            }]
        })
        .state('vat-info.edit', {
            parent: 'vat-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-dialog.html',
                    controller: 'VatInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['VatInfo', function(VatInfo) {
                            return VatInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('vat-info', null, { reload: 'vat-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('vat-info.delete', {
            parent: 'vat-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-delete-dialog.html',
                    controller: 'VatInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['VatInfo', function(VatInfo) {
                            return VatInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('vat-info', null, { reload: 'vat-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('congress-vat-info', {
            parent: 'administration',
            url: '/vat-info',
            params: {
                congressSpecific: true
            },
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.vatInfo.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/vat-info/vat-infos.html',
                    controller: 'VatInfoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                congressSpecific: ['$stateParams', function ($stateParams) {
                    return $stateParams.congressSpecific || false;
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('vatInfo');
                    $translatePartialLoader.addPart('chargeableItemType');
                    $translatePartialLoader.addPart('vatRateType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('congress-vat-info.new', {
            parent: 'congress-vat-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'congressSpecific', 'CongressSelector', function($stateParams, $state, $uibModal, congressSpecific, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-dialog.html',
                    controller: 'VatInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                vat: null,
                                szj: null,
                                chargeableItemType: null,
                                vatRateType: null,
                                vatExceptionReason: null,
                                id: null,
                                congress: congressSpecific ? CongressSelector.getSelectedCongress() : null
                            };
                        }
                    }
                }).result.then(function() {
                        $state.go('congress-vat-info', null, { reload: 'congress-vat-info' });
                    }, function() {
                        $state.go('congress-vat-info');
                    });
            }]
        })
        .state('congress-vat-info.edit', {
            parent: 'congress-vat-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-dialog.html',
                    controller: 'VatInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['VatInfo', function(VatInfo) {
                            return VatInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                        $state.go('congress-vat-info', null, { reload: 'congress-vat-info' });
                    }, function() {
                        $state.go('^');
                    });
            }]
        })
        .state('congress-vat-info.delete', {
            parent: 'congress-vat-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/vat-info/vat-info-delete-dialog.html',
                    controller: 'VatInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['VatInfo', function(VatInfo) {
                            return VatInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                        $state.go('congress-vat-info', null, { reload: 'congress-vat-info' });
                    }, function() {
                        $state.go('^');
                    });
            }]
        });

    }

})();
