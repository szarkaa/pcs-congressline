(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('hotel-summary', {
                parent: 'report',
                url: '/hotel-summary',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN'],
                    pageTitle: 'pcsApp.hotelSummary.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/hotel-summary/hotel-summary.html',
                        controller: 'HotelSummaryController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    listFilter: ['HotelSummaryFilter', function (HotelSummaryFilter) {
                        return HotelSummaryFilter.getHotelSummaryFilter();
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('hotelSummary');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('hotel-summary.selection', {
                parent: 'hotel-summary',
                url: '/select',
                data: {
                    authorities: ['ROLE_ADVANCED_USER','ROLE_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'listFilter', function ($stateParams, $state, $uibModal, listFilter) {
                    $uibModal.open({
                        templateUrl: 'app/entities/hotel-summary/hotel-summary-selection-dialog.html',
                        controller: 'HotelSummarySelectionDialogController',
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
