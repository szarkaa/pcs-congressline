(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendGeneralEmailToAllDialog', sendGeneralEmailToAllDialog);

    sendGeneralEmailToAllDialog.$inject = ['$uibModal'];

    function sendGeneralEmailToAllDialog($uibModal) {
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
                    templateUrl: 'app/entities/general-registration-report/send-general-email-to-all-dialog.html',
                    controller: 'SendGeneralEmailToAllDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        registrationSettings: function() {
                            return {registrationIds: scope.ids()};
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendGeneralEmailToAllDialog');
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
