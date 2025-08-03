import os
import pathlib
import subprocess
from datetime import datetime
from dotenv import load_dotenv

base_dir = pathlib.Path(__file__).parent.parent
env_path = base_dir / '.env'
load_dotenv(dotenv_path=env_path)

db_user = os.getenv('DB_USERNAME')
db_password = os.getenv('DB_PASSWORD')
db_name = os.getenv('DB_NAME')
container_name = 'mysql'

if not db_user or not db_password or not db_name:
    print("DB_USERNAME, DB_PASSWORD i/lub DB_NAME nie są ustawione.")
    exit(1)

timestamp = datetime.now().strftime('%Y-%m-%d-%H-%M-%S')
backup_location = 'db_backups'
os.makedirs(backup_location, exist_ok=True)
backup_filename = os.path.join(backup_location, f'{timestamp}.sql')

print(f"Creating database backup to file: {backup_filename}")
with open(backup_filename, 'w') as f:
    result = subprocess.run(
        [
            'docker', 'exec', container_name,
            '/usr/bin/mysqldump',
            '-u', db_user,
            f'-p{db_password}',
            db_name
        ],
        stdout=f,
        stderr=subprocess.PIPE
    )

if result.returncode != 0:
    print("Failed to create database backup.")
    print(result.stderr.decode())
    if os.path.exists(backup_filename):
        os.remove(backup_filename)
    exit(1)

if os.path.getsize(backup_filename) == 0:
    print("Backup file is empty — something went wrong.")
    os.remove(backup_filename)
    exit(1)

print(f"Successfully created database backup to file: {backup_filename}")
