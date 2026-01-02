(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceReportController', InvoiceReportController);

    InvoiceReportController.$inject = ['$scope', '$state', 'reportFilter', 'InvoiceReport', 'DTOptionsBuilder',
        'DTColumnDefBuilder', 'DateUtils', 'B64Encoder', 'CongressSelector'];

    function InvoiceReportController ($scope, $state, reportFilter, InvoiceReport, DTOptionsBuilder,
        DTColumnDefBuilder, DateUtils, B64Encoder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [2, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('scrollY', '500px');
        vm.dtOptions.withOption('scrollX', true);

        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(0).notSortable()
        ];

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        vm.openCalendar = openCalendar;
        vm.getInvoiceReportItemVatNettoValue = getInvoiceReportItemVatNettoValue;
        vm.getInvoiceReportItemVatValue = getInvoiceReportItemVatValue;
        vm.getInvoiceReportVatNettoTotal = getInvoiceReportVatNettoTotal;
        vm.getInvoiceReportVatValueTotal = getInvoiceReportVatValueTotal;
        vm.downloadPdf = downloadPdf;
        vm.isSendToNavButtonVisible = isSendToNavButtonVisible;
        vm.hasInvoiceSendingStatus = hasInvoiceSendingStatus;

        vm.filterProforma = true;

        vm.reportFilter = reportFilter;

        vm.search = search;
        vm.clear = clear;
        vm.isAnyFilterSet = isAnyFilterSet;
        vm.downloadReportXls = downloadReportXls;
        vm.downloadAccountantReportXls = downloadAccountantReportXls;
        vm.downloadNavXmlArchive = downloadNavXmlArchive;

        vm.reportList = [];
        vm.reportListVatColumn = [];

        function getInvoiceReportItemByVat(invoiceReport, vat) {
            var item;
            for (var i = 0; i < invoiceReport.items.length; i++) {
                if (invoiceReport.items[i].vat === vat) {
                    item = invoiceReport.items[i];
                }
            }
            return item;
        }

        function getInvoiceReportItemVatNettoValue(invoiceReport, vat) {
            var item = getInvoiceReportItemByVat(invoiceReport, vat);
            return item ? item.vatBase : '';
        }

        function getInvoiceReportItemVatValue(invoiceReport, vat) {
            var item = getInvoiceReportItemByVat(invoiceReport, vat);
            return item ? item.vatValue : '';
        }

        function getInvoiceReportVatNettoTotal(invoiceReport) {
            var value = 0.0;
            for (var i = 0; i < invoiceReport.items.length; i++) {
                value += invoiceReport.items[i].vatBase;
            }
            return value;
        }

        function getInvoiceReportVatValueTotal(invoiceReport) {
            var value = 0.0;
            for (var i = 0; i < invoiceReport.items.length; i++) {
                value += invoiceReport.items[i].vatValue;
            }
            return value;
        }

        function getInvoiceReportItemByVat(invoiceReport, vat) {
            var item;
            for (var i = 0; i < invoiceReport.items.length; i++) {
                if (invoiceReport.items[i].vat === vat) {
                    item = invoiceReport.items[i];
                }
            }
            return item;
        }

        function clear() {
            vm.reportList = [];
            vm.reportListVatColumn = [];
            vm.reportFilter.programNumber = null;
            vm.reportFilter.invoiceNumber = null;
            vm.reportFilter.fromDate = null;
            vm.reportFilter.toDate = null;
            vm.reportFilter.filterProforma = true;
            // search();
        }

        function buildReportFilter(reportFilter) {
            return {
                programNumber: reportFilter.programNumber,
                invoiceNumber: reportFilter.invoiceNumber,
                fromDate: DateUtils.convertLocalDateToServer(reportFilter.fromDate),
                toDate: DateUtils.convertLocalDateToServer(reportFilter.toDate),
                filterProforma: reportFilter.filterProforma
            };
        }

        function search() {
            InvoiceReport.query({
                    query: B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)))
                },
                function (result) {
                    vm.reportList = result;
                    var vatSet = new Set();
                    for (var i = 0; i < vm.reportList.length; i++) {
                        for (var j = 0; j < vm.reportList[i].items.length; j++) {
                            vatSet.add(vm.reportList[i].items[j].vat);
                        }
                    }
                    vm.reportListVatColumn = Array.from(vatSet);
                    vm.reportListVatColumn.sort();
                }
            );
        }

        function downloadReportXls () {
            window.location.href = '/api/invoice-report/download-report?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function downloadAccountantReportXls () {
            window.location.href = '/api/invoice-report/download-accountant-report?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function downloadNavXmlArchive () {
            window.location.href = '/api/invoice-report/download-nav-xml-archive?query=' + B64Encoder.encode(JSON.stringify(buildReportFilter(vm.reportFilter)));
        }

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function downloadPdf (invoiceId) {
            window.open('/api/invoice-report/' + invoiceId + '/pdf', '_blank');
        }

        function isAnyFilterSet() {
            return vm.reportFilter.programNumber || vm.reportFilter.invoiceNumber || vm.reportFilter.fromDate || vm.reportFilter.toDate;
        }

        function isSendToNavButtonVisible(invoiceType, status) {
            return invoiceType !== 'PRO_FORMA' && status !== 'DONE' && status !== 'SENT';
        }

        function hasInvoiceSendingStatus(status) {
            return status == 'ABORTED' || status == 'DONE' || status == 'SAVED';
        }
    }
})();
