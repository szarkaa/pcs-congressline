(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bank-account', {
            parent: 'maintenance',
            url: '/bank-account',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.bankAccount.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/bank-account/bank-accounts.html',
                    controller: 'BankAccountController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bankAccount');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bank-account.new', {
            parent: 'bank-account',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bank-account/bank-account-dialog.html',
                    controller: 'BankAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                bankName: null,
                                bankAddress: null,
                                bankAccount: null,
                                swiftCode: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bank-account', null, { reload: 'bank-account' });
                }, function() {
                    $state.go('bank-account');
                });
            }]
        })
        .state('bank-account.edit', {
            parent: 'bank-account',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bank-account/bank-account-dialog.html',
                    controller: 'BankAccountDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BankAccount', function(BankAccount) {
                            return BankAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bank-account', null, { reload: 'bank-account' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bank-account.delete', {
            parent: 'bank-account',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bank-account/bank-account-delete-dialog.html',
                    controller: 'BankAccountDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BankAccount', function(BankAccount) {
                            return BankAccount.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bank-account', null, { reload: 'bank-account' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
