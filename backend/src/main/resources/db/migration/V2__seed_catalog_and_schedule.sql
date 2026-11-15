INSERT INTO services (name, description, price, duration_minutes, active) VALUES
('Corte clasico', 'Corte de cabello personalizado con lavado y peinado final.', 25000.00, 45, TRUE),
('Corte + barba', 'Corte de cabello y perfilado de barba con toalla caliente.', 38000.00, 60, TRUE),
('Perfilado de barba', 'Diseno y perfilado de barba con navaja y productos hidratantes.', 18000.00, 30, TRUE),
('Afeitado tradicional', 'Afeitado con navaja, toalla caliente y masaje facial.', 22000.00, 40, TRUE),
('Corte infantil', 'Corte de cabello para ninos con atencion personalizada.', 20000.00, 30, TRUE);

INSERT INTO business_hours (day_of_week, open_time, close_time, active) VALUES
(1, '09:00:00', '19:00:00', TRUE),
(2, '09:00:00', '19:00:00', TRUE),
(3, '09:00:00', '19:00:00', TRUE),
(4, '09:00:00', '19:00:00', TRUE),
(5, '09:00:00', '19:00:00', TRUE),
(6, '09:00:00', '18:00:00', TRUE),
(7, '09:00:00', '13:00:00', FALSE);
