(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('sendMiscFinancialNoticeToSelectedDialog', sendMiscFinancialNoticeToSelectedDialog);

    sendMiscFinancialNoticeToSelectedDialog.$inject = ['$uibModal'];

    function sendMiscFinancialNoticeToSelectedDialog($uibModal) {
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
                    templateUrl: 'app/entities/misc-invoice/send-misc-financial-notice-to-selected-dialog.html',
                    controller: 'SendMiscFinancialNoticeToSelectedDialogController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        financialReportSettings: function() {
                            return {
                                sendAllEmail: '',
                                language: 'hu',
                                optionalText: '',
                                miscInvoiceIds: scope.ids(),
                                congressId: null
                            };
                        },
                        translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                            $translatePartialLoader.addPart('sendMiscFinancialNoticeToSelectedDialog');
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
