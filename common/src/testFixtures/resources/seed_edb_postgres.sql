CREATE SCHEMA astd1_asset;
CREATE SCHEMA cord1_asset_cur;
CREATE SCHEMA cord1_client_cur;
CREATE SCHEMA business_object_1;
CREATE SCHEMA refd1_reference_cur;
CREATE SCHEMA txnd1_driver_mileage_cur;

CREATE TABLE astd1_asset.veh_odom
(
    ast_id                int4                                NOT NULL,
    odom_rdng_dt          timestamp                           NOT NULL,
    odom_rdng_typ_cd      varchar(10)                         NOT NULL,
    row_add_dt            timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    odom_rdng_amt         int4                                NOT NULL,
    bat_id                int4                                NULL,
    seq_no                int4                                NULL,
    odom_rdng_qlty_val    int2      DEFAULT 0                 NULL,
    odom_rdng_qlty_run_dt timestamp                           NULL,
    audit_insert_dt       timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    audit_insert_login    varchar(30)                         NULL,
    audit_insert_pgm      varchar(30)                         NULL,
    audit_update_dt       timestamp                           NULL,
    audit_update_login    varchar(30)                         NULL,
    audit_update_pgm      varchar(30)                         NULL
);
CREATE INDEX veh_odom_02 ON astd1_asset.veh_odom USING btree (bat_id, seq_no);
CREATE UNIQUE INDEX veh_odom_i01 ON astd1_asset.veh_odom USING btree (ast_id, odom_rdng_dt, odom_rdng_typ_cd, odom_rdng_amt);

CREATE TABLE cord1_asset_cur.dan_xref
(
    edb_asset_id         int4                                      NOT NULL,
    spin_asset_id        int4                                      NOT NULL,
    ast_del_from_src_ind varchar(1) DEFAULT 'N'::character varying NULL
);
CREATE UNIQUE INDEX dan_xref_i01 ON cord1_asset_cur.dan_xref USING btree (edb_asset_id, spin_asset_id);

CREATE TABLE cord1_client_cur.cli_no_xref
(
    SPIN_org_id          int                                 NOT NULL,
    corp_cd              char(2)                             NOT NULL,
    cli_no               varchar(8)                          NOT NULL,
    SPIN_audit_insert_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    SPIN_audit_update_dt timestamp                           NULL,
    src_extract_dt       timestamp                           NOT NULL,
    src_add_dt           timestamp                           NOT NULL,
    src_del_dt           timestamp                           NULL,
    cli_del_from_src_ind char(1)   DEFAULT 'N'               NOT NULL,
    edb_org_id           int       DEFAULT 0                 NULL
);
CREATE UNIQUE INDEX cli_no_xref_i01 ON cord1_client_cur.cli_no_xref USING btree (SPIN_org_id, edb_org_id);

CREATE TABLE refd1_reference_cur.sequence_generator
(
    seq_nm             varchar(30)                         NOT NULL,
    last_seq_no        int       DEFAULT 0                 NOT NULL,
    audit_insert_dt    timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    audit_insert_login varchar(30)                         NOT NULL,
    audit_insert_pgm   varchar(30)                         NOT NULL,
    audit_update_dt    timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    audit_update_login varchar(30)                         NULL,
    audit_update_pgm   varchar(30)                         NULL,
    seq_no_prefix      char(3)                             NULL,
    CONSTRAINT sequence_generator_pk PRIMARY KEY (seq_nm)
);
INSERT INTO refd1_reference_cur.sequence_generator (seq_nm, audit_insert_login, audit_insert_pgm, audit_insert_dt)
VALUES ('mileage_report', 'test_user', 'iwd', CURRENT_TIMESTAMP);
INSERT INTO refd1_reference_cur.sequence_generator (seq_nm, audit_insert_login, audit_insert_pgm, audit_insert_dt)
VALUES ('mileage_report_canada', 'test_user', 'iwd', CURRENT_TIMESTAMP);

CREATE TABLE txnd1_driver_mileage_cur.mileage_report
(
    mileage_rpt_id          int                                     NOT NULL,
    psn_id                  int           DEFAULT 0                 NOT NULL,
    ast_id                  int           DEFAULT 0                 NOT NULL,
    period_end_dt           timestamp                               NOT NULL,
    period_start_dt         timestamp                               NOT NULL,
    mileage_rpt_entry_dt    timestamp                               NOT NULL,
    bus_mileage_amt         decimal(8, 1) DEFAULT 0                 NOT NULL,
    psn_mileage_amt         decimal(8, 1) DEFAULT 0                 NOT NULL,
    total_mileage_amt       decimal(8, 1) DEFAULT 0                 NOT NULL,
    distance_uom_cd         char(2)       DEFAULT ' '               NOT NULL,
    days_in_veh             smallint      DEFAULT 0                 NOT NULL,
    end_odom_reading_amt    decimal(8, 1) DEFAULT 0                 NOT NULL,
    begin_odom_reading_amt  decimal(8, 1)                           NULL,
    rpt_period_typ_cd       smallint      DEFAULT 0                 NOT NULL,
    mileage_rpt_source_cd   smallint      DEFAULT 0                 NOT NULL,
    psn_org_id              int           DEFAULT 0                 NOT NULL,
    psn_corp_cd             char(2)       DEFAULT ' '               NOT NULL,
    psn_cli_no              varchar(8)    DEFAULT ' '               NOT NULL,
    psn_bkdn                varchar(45)   DEFAULT ' '               NOT NULL,
    ast_org_id              int           DEFAULT 0                 NOT NULL,
    ast_corp_cd             char(2)       DEFAULT ' '               NOT NULL,
    ast_cli_no              varchar(8)    DEFAULT ' '               NOT NULL,
    ast_bkdn                varchar(45)   DEFAULT ' '               NOT NULL,
    suppress_VER_export_ind int4          DEFAULT 0                 NOT NULL,
    VER_export_dt           timestamp                               NULL,
    suppress_IVR_export_ind int2          DEFAULT 0                 NOT NULL,
    IVR_export_dt           timestamp                               NULL,
    row_del_ind             int2          DEFAULT 0                 NOT NULL,
    audit_insert_dt         timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    audit_insert_login      varchar(30)                             NOT NULL,
    audit_insert_pgm        varchar(30)                             NOT NULL,
    audit_update_dt         timestamp     DEFAULT CURRENT_TIMESTAMP NULL,
    audit_update_login      varchar(30)                             NULL,
    audit_update_pgm        varchar(30)                             NULL,
    commuter_trips_qty      smallint                                NULL,
    CONSTRAINT mileage_report_pk PRIMARY KEY (mileage_rpt_id)
);
CREATE UNIQUE INDEX mileage_report_index ON txnd1_driver_mileage_cur.mileage_report (psn_id, ast_id, period_end_dt, mileage_rpt_id);


CREATE TABLE txnd1_driver_mileage_cur.mileage_report_detail
(
    mileage_rpt_id     int                                 NOT NULL,
    psn_id             int                                 NOT NULL,
    ast_id             int                                 NOT NULL,
    exp_typ_cd         char(4)                             NOT NULL,
    psn_corp_cd        char(2)                             NOT NULL,
    period_end_dt      timestamp                           NOT NULL,
    period_start_dt    timestamp                           NOT NULL,
    uom_typ_cd         varchar(4)                          NOT NULL,
    lang_typ_cd        char(1)                             NOT NULL,
    exp_amt            money                               NOT NULL,
    exp_qnty           int                                 NOT NULL,
    audit_insert_dt    timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    audit_insert_login varchar(30)                         NOT NULL,
    audit_insert_pgm   varchar(30)                         NOT NULL,
    audit_update_dt    timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    audit_update_login varchar(30)                         NULL,
    audit_update_pgm   varchar(30)                         NULL,
    CONSTRAINT mileage_report_detail_fk FOREIGN KEY (mileage_rpt_id) REFERENCES txnd1_driver_mileage_cur.mileage_report (mileage_rpt_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE UNIQUE INDEX mileage_report_detail_index ON txnd1_driver_mileage_cur.mileage_report_detail (psn_id, ast_id, exp_typ_cd, psn_corp_cd, period_end_dt);

CREATE FUNCTION public.getdate() RETURNS TIMESTAMPTZ
    STABLE
    LANGUAGE sql as
'SELECT now()';