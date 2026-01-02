(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('base', {
            abstract: true,
            views: {
                'base@': {
                    templateUrl: 'app/layouts/content/content.html'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        });

        $stateProvider.state('app', {
            parent: 'base',
            abstract: true,
            views: {
                'navbar@base': {
                    templateUrl: 'app/layouts/navbar/navbar.html',
                    controller: 'NavbarController',
                    controllerAs: 'vm'
                },
                'header@base': {
                    templateUrl: 'app/layouts/header/header.html',
                    controller: 'HeaderController',
                    controllerAs: 'hd'
                },
                'footer@base': {
                    templateUrl: 'app/layouts/footer/footer.html',
                    controller: 'FooterController',
                    controllerAs: 'ft'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        });
    }
})();
