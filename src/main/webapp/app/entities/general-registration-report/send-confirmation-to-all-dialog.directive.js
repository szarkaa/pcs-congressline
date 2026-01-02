(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendConfirmationToAllDialog', sendConfirmationToAllDialog);

    sendConfirmationToAllDialog.$inject = ['$uibModal'];

    function sendConfirmationToAllDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude>{{email}}</a>',
            scope: {
                filter: "="
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/entities/general-registration-report/send-confirmation-to-all-dialog.html',
                    controller: 'SendConfirmationToAllDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        confirmationSettings: function() {
                            return {filter: scope.filter};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendConfirmationToAllDialog');
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
