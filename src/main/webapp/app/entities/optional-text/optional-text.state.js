(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('optional-text', {
            parent: 'administration',
            url: '/optional-text',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.optionalText.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/optional-text/optional-texts.html',
                    controller: 'OptionalTextController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('optionalText');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('optional-text.new', {
            parent: 'optional-text',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'CongressSelector', function($stateParams, $state, $uibModal, CongressSelector) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                    controller: 'OptionalTextDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                optionalText: null,
                                id: null,
                                congress: CongressSelector.getSelectedCongress()
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('optional-text', null, { reload: 'optional-text' });
                }, function() {
                    $state.go('optional-text');
                });
            }]
        })
        .state('optional-text.edit', {
            parent: 'optional-text',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-text/optional-text-dialog.html',
                    controller: 'OptionalTextDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OptionalText', function(OptionalText) {
                            return OptionalText.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('optional-text', null, { reload: 'optional-text' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('optional-text.delete', {
            parent: 'optional-text',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/optional-text/optional-text-delete-dialog.html',
                    controller: 'OptionalTextDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OptionalText', function(OptionalText) {
                            return OptionalText.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('optional-text', null, { reload: 'optional-text' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
