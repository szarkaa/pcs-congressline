(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('regfee-details-by-participant', {
                parent: 'report',
                url: '/regfee-details-by-participant',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.regFeeDetailsByParticipantController.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/regfee-details-by-participant/regfee-details-by-participants.html',
                        controller: 'RegFeeDetailsByParticipantController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    reportFilter: function () {
                        return {
                            regType: null
                        };
                    },
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('regFeeDetailsByParticipant');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
