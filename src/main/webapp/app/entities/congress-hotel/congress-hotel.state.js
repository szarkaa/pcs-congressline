(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('congress-hotel', {
            parent: 'administration',
            url: '/congress-hotel',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.congressHotel.home.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/congress-hotel/congress-hotels.html',
                    controller: 'CongressHotelController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('congressHotel');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('congress-hotel.select-hotel', {
            parent: 'congress-hotel',
            url: '/select-hotels',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pcsApp.congressHotel.selectHotel.title'
            },
            views: {
                'content@base': {
                    templateUrl: 'app/entities/congress-hotel/select-hotels.html',
                    controller: 'CongressHotelSelectionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('hotel');
                    return $translate.refresh();
                }]
            }
        });
    }

})();
