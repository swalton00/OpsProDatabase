INSERT INTO
    runids
    (
        RunId,
        comments,
        Created_Date
    )
    VALUES
    (
        'A',
        'First test',
        CURRENT_TIMESTAMP
    );
UPDATE
    runids 
SET 
    comments = 'Value'
WHERE 
    runId = 'A';
INSERT INTO
    seq_value
    (
        current_seq,
        runid
    )
    VALUES
    (
        1,
        'A'
    );
    UPDATE 
    seq_value
SET 
    current_seq = 1
WHERE 
    runid = 'A';
    SELECT 
    COUNT(*) 
FROM 
    seq_value 
WHERE 
    runId = 'A';         
MERGE
INTO
    cars ( carid, roadname, roadnumber, runid ) KEY ( runId, CarId) VALUES
    (
        'ARLX11869',
        'ARLX',
        '11869',
        'A'
    );