(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendAllConfirmationToEmailDialog', sendAllConfirmationToEmailDialog);

    sendAllConfirmationToEmailDialog.$inject = ['$uibModal'];

    function sendAllConfirmationToEmailDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude>{{email}}</a>',
            scope: {
                ids: "&"
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/entities/general-registration-report/send-all-confirmation-to-email-dialog.html',
                    controller: 'SendAllConfirmationToEmailDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        confirmationSettings: function(){
                            return {registrationIds: scope.ids()};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendAllConfirmationToEmailDialog');
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
