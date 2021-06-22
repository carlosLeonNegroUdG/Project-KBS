
(deftemplate productBarnes
	(slot part-number)
	(multislot name)
	(slot category)
	(slot price)
	(slot quantity)
)

(deftemplate customerBarnes
	(multislot name)
	(multislot targeta)
)

(deftemplate orderBarnes
	(multislot nameCustomer)
	(multislot nameProduct)
)