INSERT INTO vat_info (id,name,vat,szj,chargeable_item_type,congress_id,vat_rate_type,vat_exception_reason) VALUES
(1,'27%',27,NULL,'REGISTRATION',NULL,'REGULAR',NULL),
(3,'27%',27,NULL,'OPTIONAL_SERVICE',NULL,'REGULAR',NULL),
(5,'27%',27,NULL,'MISCELLANEOUS',NULL,'REGULAR',NULL),
(7,'5 %',5,NULL,'HOTEL',NULL,'REGULAR',NULL),
(9,'5 %',5,NULL,'OPTIONAL_SERVICE',NULL,'REGULAR',NULL),
(11,'ROSSZEU Reverse VAT/EU fordított áfa',0,NULL,'MISCELLANEOUS',NULL,'REGULAR',NULL),
(13,'ROSSZ VAT out of scope/Áfa körön kívűl',0,NULL,'MISCELLANEOUS',NULL,'REGULAR',NULL),
(21,'5 %',5,NULL,'MISCELLANEOUS',NULL,'REGULAR',NULL);

INSERT INTO vat_info (id,name,vat,szj,chargeable_item_type,congress_id,vat_rate_type,vat_exception_reason) VALUES
(25,'EUFAD37-Áfa tv. 37. §-a alapján másik tagállamban teljesített, fordítottan adózó ügylet/reverse charge within the EU',0,NULL,'MISCELLANEOUS',NULL,'EUFAD37','EUFAD37-Áfa tv. 37. §-a alapján másik tagállamban teljesített, fordítottan adózó ügylet/reverse charge within the EU'),
(27,'HO-Harmadik országfelé teljesített ügylet/Third country transaction',0,NULL,'MISCELLANEOUS',NULL,'HO','HO-Harmadik országfelé teljesített ügylet/Third country transaction'),
(29,'ATK-Áfa tárgyi hatályán kívül/out of material scope of VAT',0,NULL,'MISCELLANEOUS',NULL,'ATK','ATK-Áfa tárgyi hatályán kívül/out of material scope of VAT'),
(31,'EUFAD37-Áfa tv. 37. §-a alapján másik tagállamban teljesített, fordítottan adózó ügylet/reverse charge within the EU',0,NULL,'REGISTRATION',NULL,'EUFAD37','EUFAD37-Áfa tv. 37. §-a alapján másik tagállamban teljesített, fordítottan adózó ügylet/reverse charge within the EU'),
(33,'HO-Harmadik országfelé teljesített ügylet/Third country transaction',0,NULL,'REGISTRATION',NULL,'HO','HO-Harmadik országfelé teljesített ügylet/Third country transaction'),
(36,'Hotel reg',5,NULL,'REGISTRATION',NULL,'REGULAR',NULL);
