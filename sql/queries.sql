--SELECT *
--FROM Plane; 

--testing for number 6 
--SELECT (p.seats - f.num_sold) AS Seats_Available 
--FROM Flight f
--INNER JOIN Schedule s ON s.flightNum = f.fnum 
--INNER JOIN FlightInfo FI on FI.flight_id = f.fnum 
--INNER JOIN Plane p ON p.id = FI.plane_id 
--WHERE f.fnum = 1553 AND f.actual_departure_date = '2014-05-19'; 

--testing for number 6
--SELECT plane_id 
--FROM FlightInfo 
--WHERE flight_id = 1553; 
