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


--number 5
--SELECT * --plane.seats
--FROM FlightInfo Info,Plane plane
--WHERE Info.plane_id = plane.id AND Info.flight_id = 12;

--SELECT * --flight.num_sold
--FROM Flight flight
--WHERE flight.fnum = 12;

--SELECT  (SELECT plane.seats
--FROM FlightInfo Info,Plane plane
--WHERE Info.plane_id = plane.id AND Info.flight_id = 12) - (SELECT flight.num_sold
--FROM Flight flight
--WHERE flight.fnum = 12) AS remainin_seats;

--INSERT INTO Customer VALUES (250, 'andrea', 'cruz', 'F', '1993-05-10', '8961 satinwood', 9092021122, 92335); 
--SELECT *
--FROM Customer
--where Customer.id = 250;
SELECT * 
FROM Reservation r
WHERE r.fid = 1;

SELECT *
FROM Flight f
WHERE f.fnum = 1;

SELECT *
FROM Customer c
WHERE c.id = 250;

--DROP SEQUENCE IF EXISTS test;
--CREATE SEQUENCE rnum_seq START WITH 1000;

--SELECT nextval('test');
--DROP LANGUAGE IF EXISTS plpgsql;
--CREATE LANGUAGE plpgsql;

---last try-----
--DROP TRIGGER IF EXISTS rnum_seq ON Reservation;
--CREATE OR REPLACE FUNCTION my_seq() RETURNS trigger AS $rnum$ 
--BEGIN
--	IF  EXISTS ( SELECT 0 FROM pg_class WHERE relname = 'rnum_seq')
--        THEN
--	new.rnum = nextval('rnum_seq');
--	END IF;
--	RETURN new;
--END;
--$rnum$ LANGUAGE 'plpgsql';
--CREATE TRIGGER rnum_seq BEFORE INSERT ON Reservation FOR EACH ROW EXECUTE PROCEDURE my_seq();

