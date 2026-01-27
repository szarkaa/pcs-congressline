(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('hotel-general-report', {
                parent: 'report',
                url: '/hotel-general-report',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.hotelGeneralReport.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/hotel-general-report/hotel-general-reports.html',
                        controller: 'HotelGeneralReportController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('hotelGeneralReport');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }


})();
