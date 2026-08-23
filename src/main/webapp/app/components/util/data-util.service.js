(function() {
    'use strict';

    angular
        .module('pcsApp')
        .factory('DataUtils', DataUtils);

    DataUtils.$inject = ['$window'];

    function DataUtils ($window) {

        var service = {
            abbreviate: abbreviate,
            byteSize: byteSize,
            openFile: openFile,
            downloadFile: downloadFile,
            toBase64: toBase64
        };

        return service;

        function abbreviate (text) {
            if (!angular.isString(text)) {
                return '';
            }
            if (text.length < 30) {
                return text;
            }
            return text ? (text.substring(0, 15) + '...' + text.slice(-10)) : '';
        }

        function byteSize (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }

            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }

            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }

            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }

            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ') + ' bytes';
            }

            return formatAsBytes(size(base64String));
        }

        function createBlob(type, data) {
            var byteCharacters = atob(data);
            var byteArrays = [];
            var sliceSize = 512;

            for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
                var slice = byteCharacters.slice(offset, offset + sliceSize);
                var byteNumbers = new Array(slice.length);

                for (var i = 0; i < slice.length; i++) {
                    byteNumbers[i] = slice.charCodeAt(i);
                }

                byteArrays.push(new Uint8Array(byteNumbers));
            }

            return new Blob(byteArrays, { type: type });
        }

        function openFile(type, data) {
            var blob = createBlob(type, data);
            var url = $window.URL.createObjectURL(blob);

            var newWindow = $window.open('', '_blank');

            if (newWindow) {
                newWindow.location.href = url;
            }
        }

        function downloadFile(type, data, fileName) {
            var blob = createBlob(type, data);
            var url = $window.URL.createObjectURL(blob);

            var link = document.createElement('a');

            link.href = url;
            link.download = fileName || 'download';
            link.style.display = 'none';

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            // Do NOT revoke immediately.
            // Give the browser time to start the download.
            setTimeout(function() {
                $window.URL.revokeObjectURL(url);
            }, 1000);
        }

        function toBase64 (file, cb) {
            var fileReader = new FileReader();
            fileReader.readAsDataURL(file);
            fileReader.onload = function (e) {
                var base64Data = e.target.result.substr(e.target.result.indexOf('base64,') + 'base64,'.length);
                cb(base64Data);
            };
        }
    }
})();
