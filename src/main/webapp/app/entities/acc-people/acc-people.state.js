(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('acc-people', {
            parent: 'registration',
            url: '/registration-types/{regTypeId}/acc-peoples',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.accPeople.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/acc-people/acc-people.html',
                    controller: 'AccPeopleController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                registrationRegistrationType: ['$stateParams', 'RegistrationRegistrationType', function ($stateParams, RegistrationRegistrationType) {
                    return RegistrationRegistrationType.get({id: $stateParams.regTypeId});
                }],
                accPeoples: ['$stateParams', 'AccPeople', function ($stateParams, AccPeople) {
                    return AccPeople.queryByRegistrationRegistrationType({id: $stateParams.regTypeId});
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('accPeople');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('acc-people.new', {
            parent: 'acc-people',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
                onEnter: ['$stateParams', '$state', '$uibModal', 'registrationRegistrationType', function($stateParams, $state, $uibModal, registrationRegistrationType) {
                $uibModal.open({
                    templateUrl: 'app/entities/acc-people/acc-people-dialog.html',
                    controller: 'AccPeopleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                lastName: null,
                                firstName: null,
                                id: null,
                                registrationRegistrationType: registrationRegistrationType
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('acc-people', null, { reload: 'acc-people' });
                }, function() {
                    $state.go('acc-people');
                });
            }]
        })
        .state('acc-people.edit', {
            parent: 'acc-people',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acc-people/acc-people-dialog.html',
                    controller: 'AccPeopleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AccPeople', function(AccPeople) {
                            return AccPeople.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('acc-people', null, { reload: 'acc-people' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('acc-people.delete', {
            parent: 'acc-people',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acc-people/acc-people-delete-dialog.html',
                    controller: 'AccPeopleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AccPeople', function(AccPeople) {
                            return AccPeople.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('acc-people', null, { reload: 'acc-people' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
