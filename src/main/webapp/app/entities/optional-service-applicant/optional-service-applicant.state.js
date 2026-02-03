(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('optional-service-applicant', {
                parent: 'report',
                url: '/optional-service-applicant',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.optionalServiceApplicant.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/optional-service-applicant/optional-service-applicants.html',
                        controller: 'OptionalServiceApplicantController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    listFilter: ['OptionalServiceApplicantFilter', function (OptionalServiceApplicantFilter) {
                        return OptionalServiceApplicantFilter.getOptionalServiceApplicantFilter();
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionalServiceApplicant');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('optional-service-applicant.selection', {
                parent: 'optional-service-applicant',
                url: '/select',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'listFilter', function ($stateParams, $state, $uibModal, listFilter) {
                    $uibModal.open({
                        templateUrl: 'app/entities/optional-service-applicant/optional-service-applicant-selection-dialog.html',
                        controller: 'OptionalServiceApplicantSelectionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            listFilter: function () {
                                return listFilter;
                            }
                        }
                    });
                }]
            });
    }


})();
