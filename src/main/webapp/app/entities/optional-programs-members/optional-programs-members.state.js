(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('optional-programs-members', {
                parent: 'report',
                url: '/optional-programs-members',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'pcsApp.optionalProgramsMembers.home.title'
                },
                views: {
                    'content@base': {
                        templateUrl: 'app/entities/optional-programs-members/optional-programs-members.html',
                        controller: 'OptionalProgramsMembersController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('optionalProgramsMembers');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
    }


})();
