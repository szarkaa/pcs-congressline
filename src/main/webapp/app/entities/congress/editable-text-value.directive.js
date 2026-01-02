(function() {
    'use strict';

    angular
        .module('pcsApp')
        .directive('editableTextValueCell', editableTextValueCell);

    editableTextValueCell.$inject = [];

    function editableTextValueCell() {
        var directive = {
            restrict: 'A',
            templateUrl: 'app/entities/congress/editable-text-value-template.html',
            scope: {
                editableTextList: "=",
                editableTextIdx: "@"
            },
            link: linkFunc,
            controller: controllerFunc
        };

        linkFunc.$inject = ['$scope'];

        function linkFunc($scope, $element) {
            $element.on('click', function ($event) {
                if ($scope.editMode()) return;
                $scope.enableEdit();
            });
        }

        controllerFunc.$inject = ['$scope', '$timeout'];

        function controllerFunc($scope, $timeout) {
            var toggling = false;

            $scope.meta = {
                mode: 'view'
            };

            $scope.enableEdit = function () {
                if (toggling) return;
                $scope.meta.mode = 'edit';
                $scope.editableTextValue = $scope.editableTextList[$scope.editableTextIdx];
                $timeout(function () {
                    // angular.element('.input-group:eq(0)>input').focus();
                    $scope.$apply()
                });
            };

            $scope.editMode = function () {
                return $scope.meta.mode === 'edit';
            };

            $scope.save = function () {
                toggling = true;
                $timeout(function () {
                    $scope.editableTextList[$scope.editableTextIdx] = $scope.editableTextValue;
                    $scope.meta.mode = 'view';
                    toggling = false;
                }, 250);
            };

            $scope.cancel = function () {
                toggling = true;
                $timeout(function () {
                    $scope.meta.mode = 'view';
                    $scope.editableTextValue = $scope.editableTextList[$scope.editableTextIdx];
                    toggling = false;
                }, 250);
            };
        }

        return directive;
    }

})();
