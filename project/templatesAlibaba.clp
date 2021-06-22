
(deftemplate productAlibaba
	(slot part-number)
	(multislot name)
	(slot category)
	(slot price)
	(slot quantity)
)

(deftemplate customerAlibaba
	(multislot name)
	(multislot targeta)
)

(deftemplate orderAlibaba
	(multislot nameCustomer)
	(multislot nameProduct)
)