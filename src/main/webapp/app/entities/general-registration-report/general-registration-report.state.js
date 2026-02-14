(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('general-registration-report', {
                parent: 'report',
                url: '/general-registration-report',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.generalRegistrationReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/general-registration-report/general-registration-reports.html',
                        controller: 'GeneralRegistrationReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: ['CongressSelector', function (CongressSelector) {
                        return {
                            regId: null,
                            lastName: null,
                            firstName: null,
                            invoiceName: null,
                            position: null,
                            otherData: null,
                            accPeopleLastName: null,
                            accPeopleFirstName: null,
                            registrationType: null,
                            workplace: null,
                            payingGroup: null,
                            optionalServices: [],
                            congressHotel: null,
                            countryNegation: false,
                            presenter: null,
                            etiquette: null,
                            closed: null,
                            onSpot: null,
                            cancelled: null,
                            congressId: CongressSelector.getSelectedCongress().id
                        };
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('generalRegistrationReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
