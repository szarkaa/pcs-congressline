(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('emailDialog', emailDialog);

    emailDialog.$inject = ['$uibModal'];

    function emailDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude>{{email}}</a>',
            scope: {
                firstName: "@",
                lastName: "@",
                email: "@"
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/services/email/email-dialog.html',
                    controller: 'EmailDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        registrationEmail: function(){
                            return {lastName: scope.lastName, firstName: scope.firstName, email: scope.email};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('emailDialog');
                            $translatePartialLoader.addPart('global');
                            return $translate.refresh();
                        }]
                    }
                }).result.then(function() {

                }, function() {

                });
            };
        }
    }
})();
