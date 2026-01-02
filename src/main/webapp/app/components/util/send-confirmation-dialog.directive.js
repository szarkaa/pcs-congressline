(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendConfirmationDialog', sendConfirmationDialog);

    sendConfirmationDialog.$inject = ['$uibModal'];

    function sendConfirmationDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude>{{email}}</a>',
            scope: {
                regId: "@",
                email: "@"
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/services/confirmation/send-confirmation-dialog.html',
                    controller: 'SendConfirmationDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        confirmationSettings: function(){
                            return {regId: scope.regId, email: scope.email};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendConfirmationDialog');
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
