import os
import subprocess
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

db_user = os.getenv('DB_USER')
db_password = os.getenv('DB_PASSWORD')
db_name = os.getenv('DB_NAME')
container_name = 'mysql'

if not db_password or not db_name:
    print("DB_PASSWORD and/or DB_NAME are not set")
    exit(1)

timestamp = datetime.now().strftime('%Y-%m-%d-%H-%M-%S')
backup_location = 'db_backups'
os.makedirs(backup_location, exist_ok=True)
backup_filename = os.path.join(backup_location, f'{timestamp}.sql')

cmd = f"docker exec {container_name} /usr/bin/mysqldump -u {db_user} -p{db_password} {db_name} > {backup_filename}"

print(f"Creating database backup to file: {backup_filename}")
result = subprocess.run(cmd, shell=True)

if result.returncode == 0:
    print(f"Successfully created database backup to file: {backup_filename}")
else:
    print(f"Failed to create database backup to file: {backup_filename}")

