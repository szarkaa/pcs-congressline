(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('online-reg-custom-answer-report', {
                parent: 'report',
                url: '/online-reg-custom-answer-report',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.onlineRegCustomAnswerReportController.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/online-reg-custom-answer-report/online-reg-custom-answer-reports.html',
                        controller: 'OnlineRegCustomAnswerReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: function () {
                        return {
                            currency: null
                        };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('onlineRegCustomAnswerReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('online-reg-custom-answer-report.detail', {
                parent: 'online-reg-custom-answer-report',
                url: '/{id}/detail',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/online-reg-custom-answer-report/online-reg-custom-answer-detail-dialog.html',
                        controller: 'OnlineRegCustomAnswerDetailDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                        }
                    }).result.then(function () {
                        $state.go('^', {}, {reload: false});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }


})();
