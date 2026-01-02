(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('regfee-details', {
                parent: 'report',
                url: '/regfee-details',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.regFeeDetailsController.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/regfee-details/regfee-details.html',
                        controller: 'RegFeeDetailsController',
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
                        $translatePartialLoader.addPart('regFeeDetails');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
