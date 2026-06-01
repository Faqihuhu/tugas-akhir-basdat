IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'MyPharmacist')
BEGIN
    CREATE DATABASE MyPharmacist;
END
GO

USE MyPharmacist;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Obat' AND xtype='U')
CREATE TABLE Obat (
    kode_obat   VARCHAR(20)  PRIMARY KEY,
    nama_obat   VARCHAR(100) NOT NULL,
    harga       DECIMAL(15,2) NOT NULL DEFAULT 0,
    stok        INT           NOT NULL DEFAULT 0,
    deskripsi   VARCHAR(255)
);
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Kasir' AND xtype='U')
CREATE TABLE Kasir (
    id_kasir    INT IDENTITY(1,1) PRIMARY KEY,
    nama_kasir  VARCHAR(100) NOT NULL
);
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='NotaPenjualan' AND xtype='U')
CREATE TABLE NotaPenjualan (
    no_nota          VARCHAR(20)   PRIMARY KEY,
    tanggal          DATE          NOT NULL,
    nama_pelanggan   VARCHAR(100),
    id_kasir         INT,
    total_pembayaran DECIMAL(15,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (id_kasir) REFERENCES Kasir(id_kasir)
);
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='DetailNota' AND xtype='U')
CREATE TABLE DetailNota (
    id_detail  INT IDENTITY(1,1) PRIMARY KEY,
    no_nota    VARCHAR(20)  NOT NULL,
    kode_obat  VARCHAR(20)  NOT NULL,
    nama_obat  VARCHAR(100) NOT NULL,
    harga_unit DECIMAL(15,2) NOT NULL,
    qty        INT           NOT NULL,
    subtotal   AS (harga_unit * qty) PERSISTED,
    FOREIGN KEY (no_nota)   REFERENCES NotaPenjualan(no_nota) ON DELETE CASCADE,
    FOREIGN KEY (kode_obat) REFERENCES Obat(kode_obat)
);
GO

IF NOT EXISTS (SELECT * FROM Kasir)
BEGIN
    INSERT INTO Kasir (nama_kasir) VALUES ('Kasir 1');
    INSERT INTO Kasir (nama_kasir) VALUES ('Kasir 2');
    INSERT INTO Kasir (nama_kasir) VALUES ('Nama Kasir');
END
GO

IF NOT EXISTS (SELECT * FROM Obat)
BEGIN
    INSERT INTO Obat VALUES ('FO-01', 'Amoxicillin', 15000, 100, 'Antibiotik');
    INSERT INTO Obat VALUES ('M-01',  'Microgynon',  15000, 50,  'Kontrasepsi oral');
    INSERT INTO Obat VALUES ('PA-01', 'Paracetamol', 5000,  200, 'Pereda nyeri & demam');
    INSERT INTO Obat VALUES ('VT-01', 'Vitamin C',   8000,  150, 'Suplemen vitamin C');
END
GO

PRINT 'Database MyPharmacist berhasil dibuat!';
GO