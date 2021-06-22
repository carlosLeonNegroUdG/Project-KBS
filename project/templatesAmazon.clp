
(deftemplate productAmazon
	(slot part-number)
	(multislot name)
	(slot category)
	(slot price)
	(slot quantity)
)

(deftemplate customerAmazon
	(multislot name)
	(multislot targeta)
)

(deftemplate orderAmazon
	(multislot nameCustomer)
	(multislot nameProduct)
)