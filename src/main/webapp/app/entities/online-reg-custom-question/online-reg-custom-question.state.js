(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('online-reg-custom-question', {
            parent: 'congress',
            url: '/{congressId}/online-reg-custom-questions',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/online-reg-custom-question/online-reg-custom-questions.html',
                    controller: 'OnlineRegCustomQuestionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                congress: ['Congress', '$stateParams', function(Congress, $stateParams) {
                    return Congress.get({id : $stateParams.congressId}).$promise;
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('onlineRegCustomQuestion');
                    $translatePartialLoader.addPart('onlineVisibility');
                    return $translate.refresh();
                }]
            }
        })
        .state('online-reg-custom-question.new', {
            parent: 'online-reg-custom-question',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-custom-question/online-reg-custom-question-dialog.html',
                    controller: 'OnlineRegCustomQuestionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null,
                                question: null,
                                questionOrder: null,
                                questionAnswers: [],
                                currency: null,
                                required: false,
                                onlineVisibility: 'VISIBLE',
                                congress: { id: $stateParams.congressId }
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('online-reg-custom-question', null, { reload: 'online-reg-custom-question' });
                }, function() {
                    $state.go('online-reg-custom-question');
                });
            }]
        })
        .state('online-reg-custom-question.edit', {
            parent: 'online-reg-custom-question',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-custom-question/online-reg-custom-question-dialog.html',
                    controller: 'OnlineRegCustomQuestionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['OnlineRegCustomQuestion', function(OnlineRegCustomQuestion) {
                            return OnlineRegCustomQuestion.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('online-reg-custom-question', null, { reload: 'online-reg-custom-question' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('online-reg-custom-question.delete', {
            parent: 'online-reg-custom-question',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/online-reg-custom-question/online-reg-custom-question-delete-dialog.html',
                    controller: 'OnlineRegCustomQuestionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['OnlineRegCustomQuestion', function(OnlineRegCustomQuestion) {
                            return OnlineRegCustomQuestion.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('online-reg-custom-question', null, { reload: 'online-reg-custom-question' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
