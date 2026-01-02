(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendFinancialNoticeToAllDialog', sendFinancialNoticeToAllDialog);

    sendFinancialNoticeToAllDialog.$inject = ['$uibModal'];

    function sendFinancialNoticeToAllDialog($uibModal) {
        var directive = {
            transclude: true,
            restrict: 'EA',
            template: '<a ng-click="open()" ng-transclude>{{email}}</a>',
            scope: {
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            scope.open = function() {
                $uibModal.open({
                    templateUrl: 'app/entities/financial-report/send-financial-notice-to-all-dialog.html',
                    controller: 'SendFinancialNoticeToAllDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendFinancialNoticeToAllDialog');
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
