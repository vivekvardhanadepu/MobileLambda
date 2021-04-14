from wsgi import db, admin, login_manager
from flask_admin.contrib.sqla import ModelView
from flask_login import UserMixin

class User(db.Model, UserMixin): 
    id = db.Column(db.Integer, primary_key = True, autoincrement = True)
    username = db.Column(db.String, unique = True, nullable = False)
    password = db.Column(db.String, nullable = False)

    # backrefs
    codes = db.relationship('Code', backref = 'user', lazy = True)

@login_manager.user_loader
def load_user(user_id): 
    return User.query.get(int(user_id))

class Code(db.Model): 
    id = db.Column(db.Integer, primary_key = True, autoincrement = True)
    name = db.Column(db.String, nullable = False)
    code = db.Column(db.String)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable = False)


all_models = [User, Code]
for model in all_models: 
    admin.add_view(ModelView(model, db.session))